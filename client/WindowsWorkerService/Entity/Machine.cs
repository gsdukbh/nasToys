namespace WindowsWorkerService.Entity;

public class Machine
{
    public long? Id { get; set; }
    public string Name { get; set; }
    public string Mac { get; set; }
    public string BroadcastIp { get; set; }
    public string Ip { get; set; }
}