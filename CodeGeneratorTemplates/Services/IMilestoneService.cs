using CodeGeneratorTemplates.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Services
{
    public interface IMilestoneService
    {
        MilestoneView SelectById(Guid id);

        MilestoneView Insert(MilestoneView view);

        IEnumerable<MilestoneView> SelectAll();

        IEnumerable<MilestoneView> SelectForItem(Guid itemId);

        bool Delete(Guid id);

        bool Update(MilestoneView view);
    }
}
