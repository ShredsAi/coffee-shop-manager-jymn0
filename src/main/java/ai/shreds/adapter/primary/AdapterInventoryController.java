package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationInventoryInputPort;
import ai.shreds.shared.dtos.SharedInventoryItemDTO;
import ai.shreds.shared.value_objects.SharedInventoryRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory/items")
@Tag(name = "Inventory Management", description = "APIs for managing inventory items")
@Validated
public class AdapterInventoryController {

    private final ApplicationInventoryInputPort inventoryService;

    public AdapterInventoryController(ApplicationInventoryInputPort inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "Get all inventory items", description = "Retrieves a list of all items in inventory")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all items")
    public ResponseEntity<List<SharedInventoryItemDTO>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Get item by ID", description = "Retrieves a specific inventory item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item found"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<SharedInventoryItemDTO> getItemById(
            @Parameter(description = "ID of the item to retrieve", required = true)
            @PathVariable("itemId") UUID itemId) {
        return ResponseEntity.ok(inventoryService.getItemById(itemId));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Update item quantity", description = "Updates the quantity of a specific inventory item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item quantity updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<SharedInventoryItemDTO> updateItemQuantity(
            @Parameter(description = "ID of the item to update", required = true)
            @PathVariable("itemId") UUID itemId,
            @Parameter(description = "New quantity parameters", required = true)
            @Valid @RequestBody SharedInventoryRequestParams params) {
        return ResponseEntity.ok(inventoryService.updateItemQuantity(itemId, params));
    }

    @GetMapping("/{itemId}/availability")
    @Operation(summary = "Check item availability", description = "Checks if an item has sufficient quantity available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability check completed"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Boolean> checkItemAvailability(
            @Parameter(description = "ID of the item to check", required = true)
            @PathVariable("itemId") UUID itemId,
            @Parameter(description = "Required quantity to check", required = true)
            @RequestParam @Min(1) int requiredQuantity) {
        return ResponseEntity.ok(inventoryService.checkItemAvailability(itemId, requiredQuantity));
    }
}