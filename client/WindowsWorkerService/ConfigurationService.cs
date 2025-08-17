using System.Text.Json;
using System.Text.Json.Serialization.Metadata;
using WindowsWorkerService.Entity;

namespace WindowsWorkerService;

/// <summary>
/// 配置文件管理服务 - 只处理ApiSettings部分
/// </summary>
public class ConfigurationService
{
    private readonly ILogger<ConfigurationService> _logger;
    private readonly string _configFilePath;

    public ConfigurationService(ILogger<ConfigurationService> logger)
    {
        _logger = logger;
        // 获取 appsettings.json 的路径
        _configFilePath = Path.Combine(AppContext.BaseDirectory, "appsettings.json");
    }

    /// <summary>
    /// 更新配置文件中的MAC地址
    /// </summary>
    /// <param name="macAddress">要保存的MAC地址</param>
    /// <returns>是否成功保存</returns>
    public async Task<bool> UpdateMacAddressAsync(string macAddress)
    {
        try
        {
            // 读取现有配置
            var apiSettings = await GetApiSettingsAsync();
            
            // 更新 MAC 地址
            apiSettings.MacAddress = macAddress;
            
            // 保存配置
            return await SaveApiSettingsAsync(apiSettings);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "保存MAC地址到配置文件时发生错误");
            return false;
        }
    }
    
    /// <summary>
    /// 读取配置文件中的MAC地址
    /// </summary>
    /// <returns>MAC地址，如果不存在则返回null</returns>
    public async Task<string> GetMacAddressAsync()
    {
        try
        {
            var apiSettings = await GetApiSettingsAsync();
            return apiSettings?.MacAddress;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "从配置文件读取MAC地址时发生错误");
            return null;
        }
    }

    /// <summary>
    /// 获取ApiSettings配置
    /// </summary>
    /// <returns>ApiSettings对象</returns>
    public async Task<ApiSettings> GetApiSettingsAsync()
    {
        try
        {
            if (!File.Exists(_configFilePath))
            {
                _logger.LogWarning("配置文件不存在，返回默认ApiSettings配置");
                return new ApiSettings();
            }
            
            var jsonContent = await File.ReadAllTextAsync(_configFilePath);
            
            // 解析整个JSON文档
            using var document = JsonDocument.Parse(jsonContent);
            
            // 检查是否存在ApiSettings节点
            if (document.RootElement.TryGetProperty("ApiSettings", out var apiSettingsElement))
            {
                var options = new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true,
                    TypeInfoResolver = new DefaultJsonTypeInfoResolver()
                };
                
                var apiSettings = JsonSerializer.Deserialize<ApiSettings>(apiSettingsElement.GetRawText(), options);
                return apiSettings ?? new ApiSettings();
            }
            else
            {
                _logger.LogWarning("配置文件中未找到ApiSettings节点，返回默认配置");
                return new ApiSettings();
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "读取ApiSettings配置时发生错误，返回默认配置");
            return new ApiSettings();
        }
    }

    /// <summary>
    /// 只保存ApiSettings部分到配置文件，保持其他配置不变
    /// </summary>
    /// <param name="apiSettings">要保存的ApiSettings</param>
    /// <returns>是否成功保存</returns>
    public async Task<bool> SaveApiSettingsAsync(ApiSettings apiSettings)
    {
        try
        {
            Dictionary<string, object> configDict;
            
            if (File.Exists(_configFilePath))
            {
                // 读取现有配置文件
                var jsonContent = await File.ReadAllTextAsync(_configFilePath);
                var optionsTo = new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true,
                    TypeInfoResolver = new DefaultJsonTypeInfoResolver()
                };
                configDict = JsonSerializer.Deserialize<Dictionary<string, object>>(jsonContent,optionsTo) 
                           ?? new Dictionary<string, object>();
            }
            else
            {
                // 创建新的配置字典
                configDict = new Dictionary<string, object>();
            }
            
            // 序列化ApiSettings为JsonElement
            var apiSettingsJson = JsonSerializer.Serialize(apiSettings, new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
                TypeInfoResolver = new DefaultJsonTypeInfoResolver()
            });
            
            var apiSettingsElement = JsonSerializer.Deserialize<JsonElement>(apiSettingsJson);
            
            // 更新或添加ApiSettings节点
            configDict["ApiSettings"] = apiSettingsElement;
            
            // 写回文件，保持格式化
            var options = new JsonSerializerOptions 
            { 
                WriteIndented = true,
                TypeInfoResolver = new DefaultJsonTypeInfoResolver(),
                Encoder = System.Text.Encodings.Web.JavaScriptEncoder.UnsafeRelaxedJsonEscaping
            };
            
            var updatedJsonContent = JsonSerializer.Serialize(configDict, options);
            await File.WriteAllTextAsync(_configFilePath, updatedJsonContent);
            
            _logger.LogInformation("ApiSettings配置保存成功");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "保存ApiSettings配置时发生错误");
            return false;
        }
    }

    /// <summary>
    /// 更新整个ApiSettings配置
    /// </summary>
    /// <param name="apiSettings">新的ApiSettings配置</param>
    /// <returns>是否成功保存</returns>
    public async Task<bool> UpdateApiSettingsAsync(ApiSettings apiSettings)
    {
        return await SaveApiSettingsAsync(apiSettings);
    }
}
