using CodeGeneratorTemplates.Services;
using CodeGeneratorTemplates.Views;
using Microsoft.AspNetCore.Mvc;
using System;

namespace CodeGeneratorTemplates.Controllers
{
    [Route("api/usercollaborators")]
    [ApiController]
    public class UserCollaboratorController : ControllerBase
    {
        private readonly IUserCollaboratorService service;

        public UserCollaboratorController(IUserCollaboratorService service)
        {
            this.service = service;
        }

        [HttpGet("{id}")]
        public ActionResult<UserView> GetUserCollaborators([FromRoute] string id)
        {
            Guid userId;
            if (Guid.TryParse(id, out userId))
            {
                return Ok(service.SelectUserCollaborators(userId));
            }
            return BadRequest("Malformed id");
        }

        [HttpPost("")]
        public ActionResult<UserCollaboratorView> PostUserCollaborator([FromBody] UserCollaboratorView view)
        {
            return Ok(service.Insert(view));
        }

        [HttpDelete("{userid}/{collaboratorid}")]
        public ActionResult<string> DeleteUserCollaborator([FromRoute] string userid, [FromRoute] string collaboratorid)
        {
            Guid userId;
            if(Guid.TryParse(userid, out userId))
            {
                Guid collaboratorId;
                if(Guid.TryParse(collaboratorid, out collaboratorId))
                {
                    if(service.Delete(userId, collaboratorId))
                    {
                        return Ok("Collaborator unlinked");
                    }
                    return NotFound("Link not found");
                }
                return BadRequest("Malformed collaborator id");
            }
            return BadRequest("Malformed user id");
        }
    }
}
