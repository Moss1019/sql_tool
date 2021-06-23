using CodeGeneratorTemplates.Services;
using CodeGeneratorTemplates.Views;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;

namespace CodeGeneratorTemplates.Controllers
{
    [Route("api/users")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private IUserService service;

        public UserController(IUserService service)
        {
            this.service = service;
        }

        [HttpGet("")]
        public ActionResult<IEnumerable<UserView>> GetUsers()
        {
            var result = service.SelectAll();
            if (result == null)
            {
                return NotFound("No users found");
            }
            return Ok(result);
        }

        [HttpGet("{id}")]
        public ActionResult<UserView> GetUserbyId([FromRoute] string id)
        {
            Guid userId;
            if (Guid.TryParse(id, out userId))
            {
                try
                {
                    return Ok(service.SelectById(userId));
                }
                catch (InvalidOperationException ex)
                {
                    return NotFound(ex.Message);
                }
            }
            return BadRequest("Malformed id");
        }

        [HttpGet("by-username/{username}")]
        public ActionResult<UserView> GetUserByUsername([FromRoute] string username)
        {
            try
            {
                return Ok(service.SelectByUsername(username));
            }
            catch(InvalidOperationException ex)
            {
                return NotFound(ex.Message);
            }
        }

        [HttpPost("")]
        public ActionResult<UserView> PostUser([FromBody] UserView view)
        {
            return Ok(service.Insert(view));
        }

        [HttpDelete("{id}")]
        public ActionResult<string> DeleteUser([FromRoute] string id)
        {
            Guid userId;
            if (Guid.TryParse(id, out userId))
            {
                if (service.Delete(userId))
                {
                    return Ok("User removed");
                }
                return BadRequest("User not found");
            }
            return BadRequest("Malformed id");
        }

        [HttpPut("")]
        public ActionResult<string> UpdateUser([FromBody] UserView view)
        {
            if(service.Update(view))
            {
                return Ok(true);
            }
            return BadRequest("User not found");
        }
    }
}
