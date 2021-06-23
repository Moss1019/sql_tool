using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Repositories
{
    public interface IUserCollaboratorRepository
    {
        UserCollaborator Insert(UserCollaborator entity);

        IEnumerable<User> SelectUserCollaborators(Guid userId);

        bool Delete(Guid userId, Guid collaboratorId);
    }
}
