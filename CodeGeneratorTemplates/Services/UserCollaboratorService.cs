using CodeGeneratorTemplates.Mappers;
using CodeGeneratorTemplates.Repositories;
using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Services
{
    public class UserCollaboratorService: IUserCollaboratorService
    {
        private IUserCollaboratorRepository repo;

        public UserCollaboratorService(IUserCollaboratorRepository repo)
        {
            this.repo = repo;
        }

        public bool Delete(Guid userId, Guid collaboratorId)
        {
            return repo.Delete(userId, collaboratorId);
        }

        public UserCollaboratorView Insert(UserCollaboratorView view)
        {
            return repo.Insert(view.ToEntity()).ToView();
        }

        public IEnumerable<UserView> SelectUserCollaborators(Guid userId)
        {
            return repo.SelectUserCollaborators(userId)
                .Select(e => e.ToView());
        }
    }
}
