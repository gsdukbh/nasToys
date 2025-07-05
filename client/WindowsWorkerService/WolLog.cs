namespace WindowsWorkerService;

public class WolLog
{
    public int Id { get; set; }
    public int DelayTime { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Mac { get; set; } = string.Empty;
    public string Parameter { get; set; } = string.Empty;
    public DateTime CreateTime { get; set; }
    public DateTime? CompletedTime { get; set; }
    public bool Status { get; set; }

    // 其他属性和方法可以根据需要添加
}