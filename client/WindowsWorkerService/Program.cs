using WindowsWorkerService;
using WindowsWorkerService.Service;

var host = Host.CreateDefaultBuilder(args)
    .UseWindowsService() // 将应用程序配置为 Windows 服务
    .ConfigureServices((hostContext, services) =>
    {
        // 从 hostContext 中获取已加载的配置
        IConfiguration configuration = hostContext.Configuration;

        
        // 注册原始的 Worker (如果还需要它的话)
        // services.AddHostedService<Worker>();
        
        // 注册配置服务
        services.AddSingleton<ConfigurationService>();
        
        // 注册 InitialApp 作为后台服务来在应用程序启动时注册设备信息
        services.AddHostedService<InitialApp>();
        
        // 注册我们的 HttpPollingWorker
        services.AddHostedService<HttpPollingWorker>();
        

        // 配置和注册 HttpClientFactory
        services.AddHttpClient("SpringBootAPI", client =>
        {
            // 从配置中读取 API 的基础地址和 Token
            var apiSettings = configuration.GetSection("ApiSettings");
            string baseUrl = apiSettings.GetValue<string>("BaseUrl");
            string token = apiSettings.GetValue<string>("Token");

            if (string.IsNullOrEmpty(baseUrl) || string.IsNullOrEmpty(token))
            {
                throw new InvalidOperationException("API 配置 (BaseUrl 或 Token) 未在 appsettings.json 中设置。");
            }

            client.BaseAddress = new Uri(baseUrl);
            client.DefaultRequestHeaders.Add("Authorization", $"Bearer {token}");
        });
    })
    .Build();

host.Run();
