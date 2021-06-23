using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Services
{
    public interface IItemCollaboratorService
    {
        ItemCollaboratorView Insert(ItemCollaboratorView view);

        IEnumerable<UserView> SelectItemCollaborators(Guid itemId);

        bool Delete(Guid itemId, Guid collaboratorId);
    }
}
