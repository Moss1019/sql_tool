using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Entities
{
    public class Item
    {
        public Guid Id { get; set; }

        public Guid OwnerId { get; set; }

        public virtual User Owner { get; set; }

        public string Title { get; set; }

        public IEnumerable<Milestone> Milestones { get; set; }
    }
}
