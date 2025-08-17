using System.Text.Json.Serialization;

namespace WindowsWorkerService.Entity;

/// <summary>
/// API 设置配置类
/// </summary>
[JsonSerializable(typeof(ApiSettings))]
public class ApiSettings
{
    public string BaseUrl { get; set; }
    public string Token { get; set; }
    public string MacAddress { get; set; }
    public long MachineId { get; set; }
    public string MachineName { get; set; }
    public string MachineBroadcastIp { get; set; }
    public int RetryCount { get; set; } = 3;
    public int Register { get; set; } = 0;
    public int RetryDelayMilliseconds { get; set; } = 2000;
}

/// <summary>
/// 应用程序配置根类
/// </summary>
public class AppSettings
{
    public ApiSettings ApiSettings { get; set; } = new ApiSettings();
}
