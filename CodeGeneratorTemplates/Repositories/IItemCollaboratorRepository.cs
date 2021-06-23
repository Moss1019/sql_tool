using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Repositories
{
    public interface IItemCollaboratorRepository
    { 
        ItemCollaborator Insert(ItemCollaborator entity);

        IEnumerable<User> SelectItemCollaborators(Guid itemId);

        bool Delete(Guid itemId, Guid collaboratorId);
    }
}
