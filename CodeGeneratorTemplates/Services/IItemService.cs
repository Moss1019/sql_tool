using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Services
{
    public interface IItemService
    {
        ItemView SelectById(Guid id);

        ItemView Insert(ItemView view);

        IEnumerable<ItemView> SelectAll();

        IEnumerable<ItemView> SelectForUser(Guid userId);

        bool Delete(Guid id);

        bool Update(ItemView view);
    }
}
