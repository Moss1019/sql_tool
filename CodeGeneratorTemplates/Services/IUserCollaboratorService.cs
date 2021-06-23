using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Services
{
    public interface IUserCollaboratorService
    {
        UserCollaboratorView Insert(UserCollaboratorView view);

        IEnumerable<UserView> SelectUserCollaborators(Guid userId);

        bool Delete(Guid userId, Guid collaboratorId);
    }
}
