using CodeGeneratorTemplates.Services;
using CodeGeneratorTemplates.Views;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CodeGeneratorTemplates.Controllers
{
    [Route("api/milestones")]
    [ApiController]
    public class MilestoneController : ControllerBase
    {
        private IMilestoneService service;

        public MilestoneController(IMilestoneService service)
        {
            this.service = service;
        }

        [HttpGet("{id}")]
        public ActionResult<MilestoneView> GetMilestoneById([FromRoute] string id)
        {
            Guid milestoneId;
            if(Guid.TryParse(id, out milestoneId))
            {
                try
                {
                    return Ok(service.SelectById(milestoneId));
                }
                catch(InvalidOperationException ex)
                {
                    return NotFound(ex.Message);
                }
            }
            return BadRequest("Malformed id");
        }

        [HttpGet("")]
        public ActionResult<IEnumerable<MilestoneView>> GetMilestones()
        {
            var result = service.SelectAll();
            if (result == null)
            {
                return NotFound("No milestones found");
            }
            return Ok(result);
        }

        [HttpGet("for-item/{id}")]
        public ActionResult<IEnumerable<MilestoneView>> GetForItem([FromRoute] string id)
        {
            Guid itemId;
            if(Guid.TryParse(id, out itemId))
            {
                return Ok(service.SelectForItem(itemId));
            }
            return BadRequest("Malformed id");
        }

        [HttpPost("")]
        public ActionResult<MilestoneView> PostMilestone([FromBody] MilestoneView view)
        {
            return Ok(service.Insert(view));
        }

        [HttpPut("")]
        public ActionResult<string> UpdateMilestone([FromBody] MilestoneView view)
        {
            if(service.Update(view))
            {
                return Ok("Milestone updated");
            }
            return NotFound("Milestone not found");
        }

        [HttpDelete("{id}")]
        public ActionResult<string> DeleteMilestone([FromRoute] string id)
        {
            Guid milestoneId;
            if(Guid.TryParse(id, out milestoneId))
            {
                if(service.Delete(milestoneId))
                {
                    return Ok("Milestone removed");
                }
                return NotFound("Milestone does not exist");
            }
            return BadRequest("Malformed id");
        }
    }
}
