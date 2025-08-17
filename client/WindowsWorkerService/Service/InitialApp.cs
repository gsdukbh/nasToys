using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization.Metadata;
using WindowsWorkerService.Entity;


namespace WindowsWorkerService.Service;

/// <summary>
/// 启动注册设备信息。
/// </summary>
public class InitialApp : BackgroundService
{
    private readonly IHttpClientFactory _httpClientFactory;
    private readonly ILogger<InitialApp> _logger;
    private readonly ConfigurationService _configurationService;

    public InitialApp(IHttpClientFactory httpClientFactory, ILogger<InitialApp> logger,
        ConfigurationService configurationService)
    {
        _httpClientFactory = httpClientFactory;
        _logger = logger;
        _configurationService = configurationService;
    }


    public async Task InitializeAsync()
    {
        // 在应用程序启动时注册设备信息
        _logger.LogInformation("应用程序启动，开始注册设备信息...");

        // 使用强类型配置服务获取完整配置
        var appSettings = await _configurationService.GetApiSettingsAsync();

        
        // 注册设备
        var r = appSettings?.Register;
        if (r is null or 0)
        {
            var macinfo = GetMacAddressInfo();
            await RegisterDeviceAsync(macinfo);
        }

        var configuredMac = appSettings?.MacAddress;
        _logger.LogInformation("配置信息 - 已配置的MAC: {configuredMac}", configuredMac);

        // 首先尝试使用配置文件中的MAC地址
        if (string.IsNullOrEmpty(configuredMac))
        {
            // 如果配置中没有MAC地址，则获取本地MAC地址
            var macinfo = GetMacAddressInfo();
            var detectedMac = macinfo.Mac;
            _logger.LogInformation("未在配置中找到 MAC 地址，使用本地检测的 MAC 地址: {mac}", detectedMac);

            // 保存到配置文件中
            if (!string.IsNullOrEmpty(detectedMac))
            {
                var saved = await _configurationService.UpdateMacAddressAsync(detectedMac);
                if (saved)
                {
                    _logger.LogInformation("MAC地址已保存到配置文件");
                }
                else
                {
                    _logger.LogWarning("无法保存MAC地址到配置文件，将使用临时值");
                }
            }
        }
    }


    public async Task RegisterDeviceAsync(Machine info)
    {
        // 从配置服务获取MAC地址（优先使用已保存的配置）
        var macAddress = await _configurationService.GetMacAddressAsync();

        // 如果配置中没有MAC地址，则获取本地MAC地址
        if (string.IsNullOrEmpty(macAddress))
        {
            macAddress = GetMacAddressInfo().Mac;
            _logger.LogInformation("使用本地检测的MAC地址进行设备注册: {mac}", macAddress);
        }
        else
        {
            _logger.LogInformation("使用配置文件中的MAC地址进行设备注册: {mac}", macAddress);
        }

        if (string.IsNullOrEmpty(macAddress))
        {
            _logger.LogError("无法获取有效的MAC地址，设备注册失败");
            return;
        }

        var appSettings = await _configurationService.GetApiSettingsAsync();
        var broadcastIp = appSettings?.MachineBroadcastIp;
        if (!String.IsNullOrEmpty(broadcastIp))
        {
            info.BroadcastIp = broadcastIp;
        }

        var macname = appSettings?.MachineName;
        if (!string.IsNullOrEmpty(macname))
        {
            info.Name = macname;
        }
        else
        {
            info.Name = Environment.MachineName;
        }

        info.Mac = macAddress;

        // 创建一个已经配置好认证信息的 HttpClient 实例
        var httpClient = _httpClientFactory.CreateClient("SpringBootAPI");
        var options = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true,
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            TypeInfoResolver = new DefaultJsonTypeInfoResolver()
        };
        String json =  JsonSerializer.Serialize(info,options);
        HttpContent body = new StringContent(json, Encoding.UTF8, "application/json");
        // 向 Spring Boot 后端发送 Post 请求
        var response = await httpClient.PostAsync($"/api/machine/registerMachine", body);

        if (response.IsSuccessStatusCode)
        {
            var responseBody = await response.Content.ReadAsStringAsync();
            var machine = JsonSerializer.Deserialize<ResultData<Machine>>(responseBody,
                new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
            if (machine?.Code == 200)
            {
                var data = machine.Data;
                appSettings.MachineId = data.Id ??0;
                appSettings.Register = 1;
                appSettings.MachineName = info.Name;
                appSettings.MachineBroadcastIp = info.BroadcastIp;
                var saved = await _configurationService.SaveApiSettingsAsync(appSettings);
                if (saved)
                {
                    _logger.LogInformation("设备注册成功，配置已保存，MAC 地址: {mac}", macAddress);
                }
                else
                {
                    _logger.LogWarning("设备注册成功，但配置保存失败，MAC 地址: {mac}", macAddress);
                }
            }
        }
        else
        {
            _logger.LogError("设备注册失败，状态码: {statusCode}", response.StatusCode);
        }
    }


    /// <summary>
    /// 新增：获取本机第一个活动的、物理网络接口的 MAC 地址。
    /// </summary>
    /// <returns>格式化后的 MAC 地址字符串，如果找不到则返回 null。</returns>
    private Machine GetMacAddressInfo()
    {
        try
        {
            // 获取所有网络接口
            var nics = NetworkInterface.GetAllNetworkInterfaces();

            // 寻找第一个符合条件的网络接口
            // 寻找第一个符合条件的网络接口
            var firstUpAndRunningNic = nics.FirstOrDefault(nic =>
                nic.OperationalStatus == OperationalStatus.Up && // 接口是活动的
                (nic.NetworkInterfaceType == NetworkInterfaceType.Ethernet ||
                 nic.NetworkInterfaceType == NetworkInterfaceType.Wireless80211) && // 是以太网或无线网卡
                nic.GetPhysicalAddress().GetAddressBytes().Length > 0 && // 确保有物理地址
                !nic.Description.Contains("Virtual") && // 关键：排除描述中包含 "Virtual" 的虚拟网卡
                !nic.Description.Contains("Loopback")); // 关键：排除环回地址

            if (firstUpAndRunningNic != null)
            {
                var physicalAddress = firstUpAndRunningNic.GetPhysicalAddress();
                var macBytes = physicalAddress.GetAddressBytes();
                var ipProperties = firstUpAndRunningNic.GetIPProperties();
                var ipv4AddressInfo = ipProperties.UnicastAddresses
                    .FirstOrDefault(addr => addr.Address.AddressFamily == AddressFamily.InterNetwork);
                IPAddress ipAddress = ipv4AddressInfo.Address;
                IPAddress subnetMask = ipv4AddressInfo.IPv4Mask;
                // 计算广播地址
                byte[] ipAddressBytes = ipAddress.GetAddressBytes();
                byte[] subnetMaskBytes = subnetMask.GetAddressBytes();

                if (ipAddressBytes.Length != subnetMaskBytes.Length)
                {
                    throw new ArgumentException("IP地址和子网掩码的字节长度不匹配。");
                }

                byte[] broadcastAddressBytes = new byte[ipAddressBytes.Length];
                for (int i = 0; i < broadcastAddressBytes.Length; i++)
                {
                    // 广播地址的计算公式:
                    // Broadcast = IP | (~Subnet)
                    // (IP地址 或 子网掩码的按位取反)
                    broadcastAddressBytes[i] = (byte)(ipAddressBytes[i] | (subnetMaskBytes[i] ^ 255));
                }

                var b = new IPAddress(broadcastAddressBytes);

                Machine res = new Machine()
                {
                    // 将字节数组格式化为标准的 MAC 地址字符串 (例如: 00-1A-2B-3C-4D-5E)
                    Mac = string.Join(":", macBytes.Select(b => b.ToString("X2"))),
                    BroadcastIp = b.ToString(),
                    Ip = ipAddress.ToString()
                };
                return res;
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "获取 MAC 地址时发生错误。");
        }

        return null;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _logger.LogInformation("InitialApp 后台服务启动，开始初始化...");

        try
        {
            // 调用初始化方法
            await InitializeAsync();


            _logger.LogInformation("InitialApp 初始化完成，设备注册任务执行完毕，服务即将退出...");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "设备注册过程中发生错误");
        }

        // 任务完成后直接退出，不需要持续运行
        _logger.LogInformation("设备注册任务完成，服务退出");
    }
}