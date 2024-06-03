package BP.infrastructure.rest;

import BP.application.dto.MenuDTO;
import BP.domain.entity.Menu;
import BP.application.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
public class MenuRestController {
    private final IMenuService service;
    private final ModelMapper mapper;

    @PostMapping()
    public ResponseEntity<MenuDTO> register(@Validated @RequestBody MenuDTO dto) throws Exception {
        Menu entity = service.save(this.convertToEntity(dto));
        return new ResponseEntity<>(this.convertToDto(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDTO> update(@PathVariable("id") Integer id, @Validated @RequestBody MenuDTO dto) throws Exception {
        Menu entity = service.update(this.convertToEntity(dto), id);
        return new ResponseEntity<>(this.convertToDto(entity), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> findById(@PathVariable("id") Integer id) throws Exception {
        Menu entity = service.readById(id);
        return new ResponseEntity<>(this.convertToDto(entity), HttpStatus.OK);
    }

    @GetMapping("/username")
    public ResponseEntity<List<Menu>> getMenusByUsername() {
        return new ResponseEntity<>(service.getMenusByUsername(), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<MenuDTO>> findAll() throws Exception {
        List<MenuDTO> dtos = service.readAll().stream().map(this::convertToDto).toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id) throws Exception {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private MenuDTO convertToDto(Menu entity) {
        return mapper.map(entity, MenuDTO.class);
    }

    private Menu convertToEntity(MenuDTO dto) {
        return mapper.map(dto, Menu.class);
    }
}
