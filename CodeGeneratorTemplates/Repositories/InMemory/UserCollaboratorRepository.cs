using CodeGeneratorTemplates.Entities;
using System;
using System.Collections.Generic;
using System.Linq;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public class UserCollaboratorRepository : BaseRepository<UserCollaborator>, IUserCollaboratorRepository
    {
        public UserCollaboratorRepository(DbContext context)
            : base(context)
        {
            collectionName = "usercollaborator";
            if (!collections.ContainsKey(collectionName))
            {
                collections[collectionName] = new List<UserCollaborator>();
            }
        }

        public bool Delete(Guid userId, Guid collaboratorId)
        {
            var entityToDelete = Get<UserCollaborator>(collectionName).Where(e => e.UserId == userId && e.CollaboratorId == collaboratorId).FirstOrDefault();
            if(entityToDelete == null)
            {
                return false;
            }
            Set(Get<UserCollaborator>(collectionName).Where(e => e.UserId != userId && e.CollaboratorId != collaboratorId).ToList());
            return true;
        }

        public UserCollaborator Insert(UserCollaborator entity)
        {
            Get<UserCollaborator>(collectionName).Add(entity);
            return entity;
        }

        public IEnumerable<User> SelectUserCollaborators(Guid userId)
        {
            var ids = Get<UserCollaborator>("usercollaborator")
                .Where(e => e.UserId == userId)
                .Select(e => e.CollaboratorId);
            return Get<User>("user")
                .Where(e => ids.Contains(e.Id));
        }
    }
}
