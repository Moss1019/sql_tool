using CodeGeneratorTemplates.Services;
using CodeGeneratorTemplates.Views;
using Microsoft.AspNetCore.Mvc;
using System;

namespace CodeGeneratorTemplates.Controllers
{
    [Route("api/itemcollaborators")]
    [ApiController]
    public class ItemCollaboratorController : ControllerBase
    {
        private readonly IItemCollaboratorService service;

        public ItemCollaboratorController(IItemCollaboratorService service)
        {
            this.service = service;
        }

        [HttpGet("{id}")]
        public ActionResult<UserView> GetCollaboratorsForItem([FromRoute] string id)
        {
            Guid itemId;
            if (Guid.TryParse(id, out itemId))
            {
                return Ok(service.SelectItemCollaborators(itemId));
            }
            return BadRequest("Malformed id");
        }

        [HttpPost("")]
        public ActionResult<ItemCollaboratorView> PostItemCollaborator([FromBody] ItemCollaboratorView view)
        {
            return Ok(service.Insert(view));
        }

        [HttpDelete("{itemid}/{collaboratorid}")]
        public ActionResult<string> DeleteItemCollaborator([FromRoute] string itemid, [FromRoute] string collaboratorid)
        {
            Guid itemId;
            if(Guid.TryParse(itemid, out itemId))
            {
                Guid collaboratorId;
                if(Guid.TryParse(collaboratorid, out collaboratorId))
                {
                    if(service.Delete(itemId, collaboratorId))
                    {
                        return Ok("Collaborator unlinked");
                    }
                    return NotFound("Link does not exist");
                }
                return BadRequest("Malformed collaborator id");
            }
            return BadRequest("Malformed item id");
        }
    }
}
