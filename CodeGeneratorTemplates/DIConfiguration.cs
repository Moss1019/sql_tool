using CodeGeneratorTemplates.Repositories;
using CodeGeneratorTemplates.Repositories.InMemory;
using CodeGeneratorTemplates.Services;
using Microsoft.Extensions.DependencyInjection;

namespace CodeGeneratorTemplates
{
    public static class DIConfiguration
    {
        public static void AddCodeGeneratorTemplatesConfiguration(this IServiceCollection services)
        {
            ConfigureRepositories(services);
            ConfigureServices(services);
        }

        private static void ConfigureRepositories(IServiceCollection services)
        {
            services.AddScoped<IUserRepository, UserRepository>();
            services.AddScoped<IItemRepository, ItemRepository>();
            services.AddScoped<IMilestoneRepository, MilestoneRepository>();
            services.AddScoped<IUserCollaboratorRepository, UserCollaboratorRepository>();
            services.AddScoped<IItemCollaboratorRepository, ItemCollaboratorRepository>();

            services.AddSingleton<DbContext>();
        }

        public static void ConfigureServices(IServiceCollection services)
        {
            services.AddScoped<IUserService, UserService>();
            services.AddScoped<IItemService, ItemService>();
            services.AddScoped<IMilestoneService, MilestoneService>();
            services.AddScoped<IUserCollaboratorService, UserCollaboratorService>();
            services.AddScoped<IItemCollaboratorService, ItemCollaboratorService>();
        }
    }
}
