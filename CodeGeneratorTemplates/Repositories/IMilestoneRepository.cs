using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Repositories
{
    public interface IMilestoneRepository
    {
        Milestone SelectById(Guid id);

        Milestone Insert(Milestone entity);

        IEnumerable<Milestone> SelectAll();

        IEnumerable<Milestone> SelectForItem(Guid itemId);

        bool Delete(Guid id);

        bool Update(Milestone entity);
    }
}
