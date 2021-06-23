using CodeGeneratorTemplates.Mappers;
using CodeGeneratorTemplates.Repositories;
using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository repo;

        public UserService(IUserRepository repo)
        {
            this.repo = repo;
        }

        public bool Delete(Guid userId)
        {
            return repo.Delete(userId);
        }

        public UserView Insert(UserView view)
        {
            view.UserId = Guid.NewGuid();
            return repo.Insert(view.ToEntity()).ToView();
        }

        public IEnumerable<UserView> SelectAll()
        {
            return repo.SelectAll()
                .Select(e => e.ToView());
        }

        public UserView SelectById(Guid id)
        {
            return repo.SelectById(id).ToView();
        }

        public UserView SelectByUsername(string username)
        {
            return repo.SelectByUserName(username).ToView();
        }

        public bool Update(UserView view)
        {
            return repo.Update(view.ToEntity());
        }
    }
}
