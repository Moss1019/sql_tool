using System;

namespace CodeGeneratorTemplates.Views
{
    public class MilestoneView
    {
        public Guid MilestoneId { get; set; } = Guid.Empty;

        public Guid ItemId { get; set; } = Guid.Empty;

        public string Description { get; set; } = string.Empty;
    }
}
