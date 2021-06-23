using System;

namespace CodeGeneratorTemplates.Views
{
    public class ItemCollaboratorView
    {
        public Guid ItemId { get; set; } = Guid.Empty;

        public Guid CollaboratorId { get; set; } = Guid.Empty;
    }
}
