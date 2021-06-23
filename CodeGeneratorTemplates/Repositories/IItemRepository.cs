using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Repositories
{
    public interface IItemRepository
    {
        Item SelectById(Guid id);

        Item Insert(Item entity);

        IEnumerable<Item> SelectAll();

        IEnumerable<Item> SelectForUser(Guid ownerId);

        bool Update(Item entity);

        bool Delete(Guid id);
    }
}
