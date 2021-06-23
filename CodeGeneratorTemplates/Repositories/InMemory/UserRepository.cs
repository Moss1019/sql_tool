using System;
using System.Linq;
using System.Collections.Generic;
using CodeGeneratorTemplates.Entities;

namespace CodeGeneratorTemplates.Repositories.InMemory
{
    public class UserRepository : BaseRepository<User>, IUserRepository
    {
        private readonly IItemRepository itemRepo;

        public UserRepository(IItemRepository itemRepo, DbContext context)
            : base(context)
        {
            this.itemRepo = itemRepo;
            collectionName = "user";
            if (!collections.ContainsKey(collectionName))
            {
                collections[collectionName] = new List<User>();
            }
        }

        public User SelectById(Guid id)
        {
            var result = Get<User>(collectionName).Where(e => e.Id == id).FirstOrDefault();
            if (result == null)
            {
                throw new InvalidOperationException("User not found");
            }
            result.Items = itemRepo.SelectForUser(result.Id);
            return result;
        }
        
        public User SelectByUserName(string userName)
        {
            var result = Get<User>(collectionName).Where(e => e.UserName == userName).FirstOrDefault();
            if(result == null)
            {
                throw new InvalidOperationException("User not found");
            }
            result.Items = itemRepo.SelectForUser(result.Id);
            return result;
        }

        public User Insert(User entity)
        {
            Get<User>(collectionName).Add(entity);
            return entity;
        }

        public IEnumerable<User> SelectAll()
        {
            var result = Get<User>(collectionName);
            foreach(var res in result)
            {
                res.Items = itemRepo.SelectForUser(res.Id);
            }
            return result;
        }

        public bool Update(User entity)
        {
            var entityToUpdate = Get<User>(collectionName).Where(e => e.Id == entity.Id).FirstOrDefault();
            if (entityToUpdate == null)
            {
                return false;
            }
            Set(Get<User>(collectionName).Where(e => e.Id != entity.Id).ToList());
            Get<User>(collectionName).Add(entity);
            return true;
        }

        public bool Delete(Guid id)
        {
            var entityToDeplete = Get<User>(collectionName).Where(e => e.Id == id).FirstOrDefault();
            if (entityToDeplete == null)
            {
                return false;
            }
            Set(Get<User>(collectionName).Where(e => e.Id != id).ToList());
            return true;
        }
    }
}
