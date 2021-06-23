using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public class ItemRepository : BaseRepository<Item>, IItemRepository
    {
        private readonly IMilestoneRepository milestoneRepo;

        public ItemRepository(IMilestoneRepository milestoneRepo, DbContext context)
            :base(context)
        {
            this.milestoneRepo = milestoneRepo;
            collectionName = "item";
            if(!collections.ContainsKey(collectionName))
            {
                collections[collectionName] = new List<Item>();
            }
        }

        public IEnumerable<Item> GetItemsForUser(Guid ownerId)
        {
            return Get<Item>(collectionName).Where(e => e.OwnerId == ownerId);
        }

        public Item Insert(Item entity)
        {
            Get<Item>(collectionName).Add(entity);
            return entity;
        }

        public IEnumerable<Item> SelectAll()
        {
            return Get<Item>(collectionName);
        }

        public Item SelectById(Guid id)
        {
            var result = Get<Item>(collectionName).Where(e => e.Id == id).FirstOrDefault();
            if (result == null)
            {
                throw new InvalidOperationException("Item not found");
            }
            result.Milestones = milestoneRepo.SelectForItem(result.Id);
            return result;
        }

        public IEnumerable<Item> SelectForUser(Guid ownerId)
        {
            var result = Get<Item>(collectionName).Where(e => e.OwnerId == ownerId);
            foreach(var res in result)
            {
                res.Milestones = milestoneRepo.SelectForItem(res.Id);
            }
            return result;
        }

        public bool Delete(Guid id)
        {
            var entityToRemove = Get<Item>(collectionName).Where(e => e.Id == id).FirstOrDefault();
            if (entityToRemove == null)
            {
                return false;
            }
            foreach(var e in milestoneRepo.SelectForItem(entityToRemove.Id))
            {
                milestoneRepo.Delete(e.Id);
            }
            Set(Get<Item>(collectionName).Where(e => e.Id != entityToRemove.Id).ToList());
            return true;
        }

        public bool Update(Item entity)
        {
            var entityToUpdate = Get<Item>(collectionName).Where(e => e.Id == entity.Id).FirstOrDefault();
            if (entityToUpdate == null)
            {
                return false;
            }
            Set(Get<Item>(collectionName).Where(e => e.Id != entityToUpdate.Id).ToList());
            Get<Item>(collectionName).Add(entity);
            return true;
        }
    }
}
