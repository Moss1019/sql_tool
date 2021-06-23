using CodeGeneratorTemplates.Entities;
using CodeGeneratorTemplates.Views;
using System.Linq;

namespace CodeGeneratorTemplates.Mappers
{
    public static class ItemMapper
    {
        public static Item ToEntity(this ItemView view)
        {
            return new Item()
            {
                Id = view.ItemId,
                Milestones = view.Milestones.Select(v => v.ToEntity()),
                OwnerId = view.OwnerId,
                Title = view.Title
            };
        }

        public static ItemView ToView(this Item entity)
        {
            return new ItemView()
            {
                ItemId = entity.Id,
                Milestones = entity.Milestones.Select(e => e.ToView()),
                OwnerId = entity.OwnerId,
                Title = entity.Title
            };
        }
    }
}
