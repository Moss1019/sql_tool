using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Views
{
    public class UserView
    {
        public Guid UserId { get; set; } = Guid.Empty;

        public string UserName { get; set; } = string.Empty;

        public IEnumerable<ItemView> Items { get; set; } = new List<ItemView>();
    }
}
