using System.Net.NetworkInformation;
using System.Text;
using System.Text.Json.Serialization.Metadata;
using WindowsWorkerService.Entity;

namespace WindowsWorkerService.Service;

using System.Text.Json;

/// <summary>
/// 定时轮询 ，接受关机命令。
/// </summary>
public class HttpPollingWorker : BackgroundService
{
    private readonly ILogger<HttpPollingWorker> _logger;
    private readonly IHttpClientFactory _httpClientFactory;
    private readonly ConfigurationService _configurationService;

    // 通过依赖注入获取日志记录器和 HttpClient 工厂
    public HttpPollingWorker(ILogger<HttpPollingWorker> logger,
        IHttpClientFactory httpClientFactory, ConfigurationService configurationService)
    {
        _logger = logger;
        _httpClientFactory = httpClientFactory;
        _configurationService = configurationService;

    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _logger.LogInformation("HTTP 轮询 Worker 启动于: {time}", DateTimeOffset.Now);

        // 当程序没有被请求停止时，循环执行
        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                // 从工厂创建一个已经配置好认证信息的 HttpClient 实例
                var httpClient = _httpClientFactory.CreateClient("SpringBootAPI");

                var appSettings = await _configurationService.GetApiSettingsAsync();
                var configuredMac = appSettings?.MacAddress;
                // 获取本地网卡的mac 
                var macAddress = "";
                if (!String.IsNullOrEmpty(configuredMac))
                {
                    macAddress = configuredMac;
                }
                else
                {
                    macAddress = GetMacAddress();
                }

                // 向 Spring Boot 后端发送 GET 请求
                var response = await httpClient.GetAsync($@"/api/wolLog?mac={macAddress}", stoppingToken);

                // 确保请求成功 (HTTP 状态码 200 OK)
                if (response.IsSuccessStatusCode)
                {
                    var responseBody = await response.Content.ReadAsStringAsync(stoppingToken);
                    var options = new JsonSerializerOptions
                    {
                        PropertyNameCaseInsensitive = true,
                        TypeInfoResolver = new DefaultJsonTypeInfoResolver()
                    };
                    var tasks = JsonSerializer.Deserialize<ResultData<WolLog>>(responseBody, options);
                    if (tasks != null && tasks.Code == 200)
                    {
                        var wolLog = tasks.Data;
                        if (wolLog.Status == false)
                        {
                            // 等待一段时间后关机。
                            _logger.LogInformation("轮询到关机任务: {name}, MAC: {mac}, 延迟时间: {delayTime}秒",
                                wolLog.Name, wolLog.Mac, wolLog.DelayTime);

                            // 先 发送成功信息。
                            wolLog.Status = true;
                            wolLog.CompletedTime = DateTime.Now;

                            // 序列化 wolLog 对象为 JSON 字符串  
                            var options1 = new JsonSerializerOptions
                            {
                                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
                            };
                            var json = JsonSerializer.Serialize(wolLog, options1);
                            // 创建 StringContent，指定内容类型为 application/json
                            var content = new StringContent(json, Encoding.UTF8, "application/json");
                            // 发送 POST 请求
                            var updateResponse =
                                await httpClient.PostAsync($@"/api/upDataWolLog", content, stoppingToken);
                            if (updateResponse.IsSuccessStatusCode)
                            {
                                _logger.LogInformation("成功更新关机任务状态: {name}", wolLog.Name);
                            }
                            else
                            {
                                _logger.LogError("更新关机任务状态失败，状态码: {statusCode}",
                                    updateResponse.StatusCode);
                            }

                            // 等待指定的延迟时间
                            await Task.Delay(wolLog.DelayTime * 1000, stoppingToken);

                            // 执行关机操作
                            _logger.LogInformation("执行关机操作...");
                            // 这里可以调用系统命令来关机，例如：
                            System.Diagnostics.Process.Start("shutdown", "/s /t 0");
                        }
                    }
                    else
                    {
                        _logger.LogWarning($@"轮询任务返回错误: {tasks.Message},本地mac：{macAddress}");
                    }
                }
                else
                {
                    _logger.LogError("请求任务失败，状态码: {statusCode}", response.StatusCode);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "轮询过程中发生错误。");
            }

            // 等待5秒后进行下一次轮询
            await Task.Delay(5000, stoppingToken);
        }
    }

    /// <summary>
    /// 新增：获取本机第一个活动的、物理网络接口的 MAC 地址。
    /// </summary>
    /// <returns>格式化后的 MAC 地址字符串，如果找不到则返回 null。</returns>
    private string GetMacAddress()
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
                // 将字节数组格式化为标准的 MAC 地址字符串 (例如: 00-1A-2B-3C-4D-5E)
                return string.Join(":", macBytes.Select(b => b.ToString("X2")));
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "获取 MAC 地址时发生错误。");
        }

        return null;
    }
}