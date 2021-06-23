using CodeGeneratorTemplates.Entities;
using CodeGeneratorTemplates.Views;

namespace CodeGeneratorTemplates.Mappers
{
    public static class ItemCollaboratorMapper
    {
        public static ItemCollaboratorView ToView(this ItemCollaborator entity)
        {
            return new ItemCollaboratorView()
            {
                CollaboratorId = entity.CollaboratorId,
                ItemId = entity.ItemId
            };
        }

        public static ItemCollaborator ToEntity(this ItemCollaboratorView view)
        {
            return new ItemCollaborator()
            {
                CollaboratorId = view.CollaboratorId,
                ItemId = view.ItemId
            };
        }
    }
}
