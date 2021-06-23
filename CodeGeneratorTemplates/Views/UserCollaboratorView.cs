using System;

namespace CodeGeneratorTemplates.Views
{
    public class UserCollaboratorView
    {
        public Guid UserId { get; set; } = Guid.Empty;

        public Guid CollaboratorId { get; set; } = Guid.Empty;
    }
}
