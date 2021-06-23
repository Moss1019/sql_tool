using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Entities
{
    public class User
    {
        public Guid Id { get; set; }

        public string UserName { get; set; }

        public IEnumerable<Item> Items { get; set; }
    }
}
