using System;

namespace CodeGeneratorTemplates.Entities
{
    public class ItemCollaborator
    {
        public Guid ItemId { get; set; }

        public virtual Item Item { get; set; }

        public Guid CollaboratorId { get; set; }

        public virtual User Collaborator { get; set; }
    }
}
