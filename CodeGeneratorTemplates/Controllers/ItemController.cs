using System;
using System.Linq;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using CodeGeneratorTemplates.Views;
using CodeGeneratorTemplates.Services;

namespace CodeGeneratorTemplates.Controllers
{
    [Route("api/items")]
    [ApiController]
    public class ItemController : ControllerBase
    {
        private readonly IItemService service;

        public ItemController(IItemService service)
        {
            this.service = service;
        }

        [HttpGet("{id}")]
        public ActionResult<ItemView> GetItemById([FromRoute] string id)
        {
            Guid itemId;
            if(Guid.TryParse(id, out itemId))
            {
                try
                {
                    return Ok(service.SelectById(itemId));
                }
                catch(InvalidOperationException ex)
                {
                    return NotFound(ex.Message);
                }
            }
            return BadRequest("Malformed id provided");
        }

        [HttpGet("for-user/{id}")]
        public ActionResult<IEnumerable<ItemView>> GetForUser([FromRoute] string id)
        {
            Guid userId;
            if(Guid.TryParse(id, out userId))
            {
                return Ok(service.SelectForUser(userId));
            }
            return BadRequest("Malformed id");
        }

        [HttpGet("")]
        public ActionResult<IEnumerable<ItemView>> GetItems()
        {
            var result = service.SelectAll();
            if(result.Count() == 0)
            {
                return NotFound("No items");
            }
            return Ok(result);
        }

        [HttpPost("")]
        public ActionResult<ItemView> PostItem([FromBody] ItemView view)
        {
            return Ok(service.Insert(view));
        }

        [HttpDelete("{id}")]
        public ActionResult<string> DeleteItem([FromRoute] string id)
        {
            Guid itemId;
            if(Guid.TryParse(id, out itemId))
            {
                if(service.Delete(itemId))
                {
                    return Ok("Item removed");
                }
                return NotFound("Item does not exist");
            }
            return BadRequest("Malformed id");
        }

        [HttpPut("")]
        public ActionResult<string> UpdateItem([FromBody] ItemView view)
        {
            if (service.Update(view))
            {
                return Ok("Item updated");
            }
            return NotFound("Item not found");
        }
    }
}
