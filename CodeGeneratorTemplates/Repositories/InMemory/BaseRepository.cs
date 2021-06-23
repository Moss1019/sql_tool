﻿using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public abstract class BaseRepository<T>
    {
        protected string collectionName;
        protected Dictionary<string, IList> collections;

        public BaseRepository(DbContext context)
        {
            collections = context.collections;
        }

        protected IList<R> Get<R>(string collection)
        {
            return (IList<R>)collections[collection];
        }

        protected void Set(IList<T> newCollection)
        {
            collections[collectionName] = (IList)newCollection;
        }
    }
}
