using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public class ItemCollaboratorRepository : BaseRepository<ItemCollaborator>, IItemCollaboratorRepository
    {
        private readonly IItemRepository itemRepo;
        private string secondaryTable = "user";

        public ItemCollaboratorRepository(IItemRepository itemRepo, DbContext context)
            : base(context)
        {
            this.itemRepo = itemRepo;
            collectionName = "itemcollaborator";
            if (!collections.ContainsKey(collectionName))
            {
                collections[collectionName] = new List<ItemCollaborator>();
            }
        }

        public bool Delete(Guid itemId, Guid collaboratorId)
        {
            var entityToDelete = Get<ItemCollaborator>(collectionName).Where(e => e.ItemId == itemId && e.CollaboratorId == collaboratorId).FirstOrDefault();
            if(entityToDelete == null)
            {
                return false;
            }
            Set(Get<ItemCollaborator>(collectionName).Where(e => e.ItemId != itemId && e.CollaboratorId != collaboratorId).ToList());
            return true;
        }

        public ItemCollaborator Insert(ItemCollaborator entity)
        {
            Get<ItemCollaborator>(collectionName).Add(entity);
            return entity;
        }

        public IEnumerable<User> SelectItemCollaborators(Guid itemId)
        {
            var ids = Get<ItemCollaborator>(collectionName)
                .Where(e => e.ItemId == itemId)
                .Select(e => e.CollaboratorId);
            return Get<User>(secondaryTable)
                .Where(e => ids.Contains(e.Id));
        }
    }
}
