using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Entities
{
    public class Milestone
    {
        public Guid Id { get; set; }

        public Guid ItemId { get; set; }

        public virtual Item Item { get; set; }

        public string Description { get; set; }
    }
}
