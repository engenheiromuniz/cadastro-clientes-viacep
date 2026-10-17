package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import one.digitalinnovation.gof.exception.CepInvalidoException;
import one.digitalinnovation.gof.exception.ClienteNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		// Buscar todos os Clientes.
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Buscar Cliente por ID.

		// --- Código refatorado após correção
//		Optional<Cliente> cliente = clienteRepository.findById(id);
//		return cliente.get();

		return clienteRepository.findById(id)
				.orElseThrow(() -> new ClienteNaoEncontradoException(id));
	}

	@Override
	public void inserir(Cliente cliente) {
		cliente.setId(null);
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		// Buscar Cliente por ID, caso exista:

		// ----> Código refatorado
//		Optional<Cliente> clienteBd = clienteRepository.findById(id);
//		if (clienteBd.isPresent()) {
//			//correção realizada e comitada
//			cliente.setId(id);
//			salvarClienteComCep(cliente);
//		}

		// ----> Método refatorado e acrescentado o método de instância abaixo
		this.buscarPorId(id);
		cliente.setId(id);
		salvarClienteComCep(cliente);
	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID.
		// ----> Método refatorado e acrescentado o método de instância abaixo
		this.buscarPorId(id);
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// ----> Código refatorado

		String cep = normalizarCep(cliente);
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			// Quando o CEP não existe, o ViaCEP responde {"erro": "true"} e os campos vêm nulos
			if (novoEndereco == null || novoEndereco.getCep() == null) {
				throw new CepInvalidoException("CEP não encontrado: " + cep);
			}
			novoEndereco.setCep(cep); // guarda sempre só com dígitos
			return enderecoRepository.save(novoEndereco);
		});
		cliente.setEndereco(endereco);
		clienteRepository.save(cliente);
	}

	private String normalizarCep(Cliente cliente) {
		if (cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
			throw new CepInvalidoException("CEP é obrigatório");
		}
		String cep = cliente.getEndereco().getCep().replaceAll("\\D", ""); // remove tudo que não é dígito
		if (cep.length() != 8) {
			throw new CepInvalidoException("CEP deve ter 8 dígitos: " + cliente.getEndereco().getCep());
		}
		return cep;
	}

}
