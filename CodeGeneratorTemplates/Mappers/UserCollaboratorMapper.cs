using CodeGeneratorTemplates.Entities;
using CodeGeneratorTemplates.Views;

namespace CodeGeneratorTemplates.Mappers
{
    public static class UserCollaboratorMapper
    {
        public static UserCollaboratorView ToView(this UserCollaborator entity)
        {
            return new UserCollaboratorView()
            {
                CollaboratorId = entity.CollaboratorId,
                UserId = entity.UserId
            };
        }

        public static UserCollaborator ToEntity(this UserCollaboratorView view)
        {
            return new UserCollaborator()
            {
                CollaboratorId = view.CollaboratorId,
                UserId = view.UserId
            };
        }
    }
}
