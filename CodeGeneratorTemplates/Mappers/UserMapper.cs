using CodeGeneratorTemplates.Entities;
using CodeGeneratorTemplates.Views;
using System.Linq;

namespace CodeGeneratorTemplates.Mappers
{
    public static class UserMapper
    {
        public static User ToEntity(this UserView view)
        {
            return new User()
            {
                Id = view.UserId,
                Items = view.Items.Select(v => v.ToEntity()),
                UserName = view.UserName
            };
        }

        public static UserView ToView(this User entity)
        {
            return new UserView()
            {
                Items = entity.Items.Select(e => e.ToView()),
                UserId = entity.Id,
                UserName = entity.UserName
            };
        }
    }
}
