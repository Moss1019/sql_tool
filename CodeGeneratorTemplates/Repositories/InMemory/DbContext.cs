using System;
using System.Collections;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public class DbContext
    {
        public Dictionary<string, IList> collections = new Dictionary<string, IList>();
    }
}
