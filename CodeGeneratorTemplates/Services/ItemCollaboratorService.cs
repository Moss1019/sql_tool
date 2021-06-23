using CodeGeneratorTemplates.Mappers;
using CodeGeneratorTemplates.Repositories;
using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Services
{
    public class ItemCollaboratorService : IItemCollaboratorService
    {
        private IItemCollaboratorRepository repo;

        public ItemCollaboratorService(IItemCollaboratorRepository repo, IUserRepository userRepo)
        {
            this.repo = repo;
        }

        public bool Delete(Guid itemId, Guid collaboratorId)
        {
            return repo.Delete(itemId, collaboratorId);
        }

        public ItemCollaboratorView Insert(ItemCollaboratorView view)
        {
            return repo.Insert(view.ToEntity()).ToView();
        }

        public IEnumerable<UserView> SelectItemCollaborators(Guid itemId)
        {
            return repo.SelectItemCollaborators(itemId)
                .Select(e => e.ToView());
        }
    }
}
