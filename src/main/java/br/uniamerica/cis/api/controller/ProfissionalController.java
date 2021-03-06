package br.uniamerica.cis.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.uniamerica.cis.api.dto.ProfissionalDTO;
import br.uniamerica.cis.api.dto.input.ProfissionalInput;
import br.uniamerica.cis.domain.exception.ResourceNotFoundException;
import br.uniamerica.cis.domain.model.Profissional;
import br.uniamerica.cis.domain.repository.ProfissionalRepository;
import br.uniamerica.cis.domain.service.ProfissionalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("Profissionais")
@RestController
@RequestMapping("/profissionais")
public class ProfissionalController implements ControllerMethods<ProfissionalDTO> {
	
	@Autowired
	private ProfissionalRepository pRepository;
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ProfissionalService pService;

	
	@ApiOperation("Cria um novo profissional")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	private EntityModel<ProfissionalDTO> criar(@Valid @RequestBody ProfissionalInput p) {
		return toModel(pService.adicionar(toEntity(p)));
	}
	
	@Override
	@GetMapping
	@ApiOperation("Retorna todos os profissionais")
	public CollectionModel<EntityModel<ProfissionalDTO>> all(){
		
		List<EntityModel<ProfissionalDTO>> list = toCollectionModel(pRepository.findAll());
		
		return new CollectionModel<>(list, 
				linkTo(methodOn(EspecialidadeController.class).all()).withSelfRel());
	}
	
	@Override
	@ApiOperation("Retorna um profissional específico")
	@GetMapping("{id}")
	public EntityModel<ProfissionalDTO> one(@PathVariable Long id) {
		
		Profissional profissional = pRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		
		return toModel(profissional);
	}
	
	private Profissional toEntity(ProfissionalInput entityDto) {
		return modelMapper.map(entityDto, Profissional.class);
	}
	
	public EntityModel<ProfissionalDTO> toModel(Profissional entity) {
		ProfissionalDTO dtoEntity = modelMapper.map(entity, ProfissionalDTO.class);
		return new EntityModel<>(dtoEntity, 
				linkTo(methodOn(ProfissionalController.class).one(dtoEntity.getId())).withSelfRel(),
				linkTo(methodOn(ProfissionalController.class).all()).withRel("profissionais"));	
	}
	
	public List<EntityModel<ProfissionalDTO>> toCollectionModel(List<Profissional> list){
		return list.stream().map(this::toModel).collect(Collectors.toList());
	}
}
