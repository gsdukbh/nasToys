namespace WindowsWorkerService;


public class WolLog
{
    public int Id { get; set; }
    public string? Name { get; set; }
    public string? Mac { get; set; }
    public DateTime? CreateTime { get; set; }
    public DateTime? CompletedTime { get; set; }
    public string? Ip { get; set; }
    public string? BroadcastIp { get; set; }
    public bool Status { get; set; }
    public int DelayTime { get; set; }
}

