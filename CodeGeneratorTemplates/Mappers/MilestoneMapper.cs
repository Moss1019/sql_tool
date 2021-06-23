using CodeGeneratorTemplates.Entities;
using CodeGeneratorTemplates.Views;

namespace CodeGeneratorTemplates.Mappers
{
    public static class MilestoneMapper
    {
        public static Milestone ToEntity(this MilestoneView view)
        {
            return new Milestone()
            {
                Description = view.Description,
                Id = view.MilestoneId,
                ItemId = view.ItemId
            };
        }

        public static MilestoneView ToView(this Milestone entity)
        {
            return new MilestoneView()
            {
                Description = entity.Description,
                MilestoneId = entity.Id,
                ItemId = entity.ItemId
            };
        }
    }
}
