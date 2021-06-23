using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Services
{
    public interface IUserService
    {
        UserView SelectById(Guid id);

        UserView SelectByUsername(string username);

        UserView Insert(UserView view);

        IEnumerable<UserView> SelectAll();

        bool Update(UserView view);

        bool Delete(Guid userId);
    }
}
