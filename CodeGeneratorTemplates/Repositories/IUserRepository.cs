using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Repositories
{
    public interface IUserRepository
    {
        User SelectById(Guid id);

        User SelectByUserName(string userName);

        User Insert(User entity);

        IEnumerable<User> SelectAll();

        bool Update(User entity);

        bool Delete(Guid id);
    }
}
