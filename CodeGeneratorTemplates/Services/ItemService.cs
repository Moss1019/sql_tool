using CodeGeneratorTemplates.Mappers;
using CodeGeneratorTemplates.Repositories;
using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Services
{
    public class ItemService : IItemService
    {
        public readonly IItemRepository repo;

        public ItemService(IItemRepository repo)
        {
            this.repo = repo;
        }

        public bool Delete(Guid id)
        {
            return repo.Delete(id);
        }

        public ItemView Insert(ItemView view)
        {
            view.ItemId = Guid.NewGuid();
            return repo.Insert(view.ToEntity()).ToView();
        }

        public IEnumerable<ItemView> SelectAll()
        {
            return repo.SelectAll()
                .Select(e => e.ToView());
        }

        public ItemView SelectById(Guid id)
        {
            return repo.SelectById(id).ToView();
        }

        public IEnumerable<ItemView> SelectForUser(Guid userId)
        {
            return repo.SelectForUser(userId)
                .Select(e => e.ToView());
        }

        public bool Update(ItemView view)
        {
            return repo.Update(view.ToEntity());
        }
    }
}
