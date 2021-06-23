using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Views
{
    public class ItemView
    {
        public Guid ItemId { get; set; } = Guid.Empty;

        public Guid OwnerId { get; set; } = Guid.Empty;

        public string Title { get; set; } = null!;

        public IEnumerable<MilestoneView> Milestones { get; set; } = new List<MilestoneView>();
    }
}
