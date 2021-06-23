using CodeGeneratorTemplates.Mappers;
using CodeGeneratorTemplates.Repositories;
using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Services
{
    public class MilestoneService : IMilestoneService
    {
        private readonly IMilestoneRepository repo;

        public MilestoneService(IMilestoneRepository repo)
        {
            this.repo = repo;
        }

        public bool Delete(Guid id)
        {
            return repo.Delete(id);
        }

        public MilestoneView Insert(MilestoneView view)
        {
            view.MilestoneId = Guid.NewGuid();
            return repo.Insert(view.ToEntity()).ToView();
        }

        public IEnumerable<MilestoneView> SelectAll()
        {
            return repo.SelectAll()
                .Select(e => e.ToView());
        }

        public MilestoneView SelectById(Guid id)
        {
            return repo.SelectById(id).ToView();
        }

        public IEnumerable<MilestoneView> SelectForItem(Guid itemId)
        {
            return repo.SelectForItem(itemId)
                .Select(e => e.ToView());
        }

        public bool Update(MilestoneView view)
        {
            return repo.Update(view.ToEntity());
        }
    }
}
