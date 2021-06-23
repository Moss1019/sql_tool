using System;

namespace CodeGeneratorTemplates.Entities
{
    public class UserCollaborator
    {
        public Guid UserId { get; set; }

        public virtual User User { get; set; }

        public Guid CollaboratorId { get; set; }

        public virtual User Collaborator { get; set; }
    }
}
