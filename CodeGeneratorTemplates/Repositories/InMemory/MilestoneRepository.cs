using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public class MilestoneRepository : BaseRepository<Milestone>, IMilestoneRepository
    {
        public MilestoneRepository(DbContext context)
            : base(context)
        {
            collectionName = "milestone";
            if (!collections.ContainsKey(collectionName))
            {
                collections[collectionName] = new List<Milestone>();
            }
        }

        public bool Delete(Guid id)
        {
            var entityToRemove = Get<Milestone>(collectionName).Where(e => e.Id == id).FirstOrDefault();
            if(entityToRemove == null)
            {
                return false;
            }
            Set(Get<Milestone>(collectionName).Where(e => e.Id != id).ToList());
            return true;
        }

        public Milestone Insert(Milestone entity)
        {
            Get<Milestone>(collectionName).Add(entity);
            return entity;
        }

        public IEnumerable<Milestone> SelectAll()
        {
            return Get<Milestone>(collectionName);
        }

        public Milestone SelectById(Guid id)
        {
            var result = Get<Milestone>(collectionName).Where(e => e.Id == id);
            if (result.Count() == 0)
            {
                throw new InvalidOperationException("Milestone not found");
            }
            return result.First();
        }

        public IEnumerable<Milestone> SelectForItem(Guid itemId)
        {
            return Get<Milestone>(collectionName).Where(e => e.ItemId == itemId);
        }

        public bool Update(Milestone entity)
        {
            var entityToUpdate = Get<Milestone>(collectionName).Where(e => e.Id == entity.Id).FirstOrDefault();
            if(entityToUpdate == null)
            {
                return false;
            }
            Set(Get<Milestone>(collectionName).Where(e => e.Id != entityToUpdate.Id).ToList());
            Get<Milestone>(collectionName).Add(entity);
            return true;
        }
    }
}
