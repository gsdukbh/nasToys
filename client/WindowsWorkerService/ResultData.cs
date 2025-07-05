namespace WindowsWorkerService;

public class ResultData<T>
{
    public int Code { get; set; } = 0;
    public string Message { get; set; } = string.Empty;
    public string Type { get; set; } = string.Empty;
    public long Timestamp { get; set; } 
    public T Data { get; set; } = default!;
}