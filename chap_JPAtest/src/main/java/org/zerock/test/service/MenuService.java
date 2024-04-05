package org.zerock.test.service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.test.domain.Menu;
import org.zerock.test.dto.MenuDTO;
import org.zerock.test.repository.MenuRepository;

import java.util.List;
import java.util.stream.Collectors;

// Service에서는 Repository에 구현 된 DB에 접근하는 여러 메소드들을 호출한 뒤
// 해당 결과를 Controller로 리턴할 것이다
// Menu Entity와 동일한 필드를 가지는 MenuDTO를 작성한다.
@Service
public class MenuService {

    //MenuRepository타입을 생성자 주입 받는
    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;

    // MenuService에서 해당 빈을 의존성 주입 받아 쓰고 싶으므로
    // 생성자를 이용한 의존성 주입을 하도록 기존의 코드를 수정
    public MenuService(MenuRepository menuRepository, ModelMapper modelMapper){
        
        this.menuRepository = menuRepository;
        this.modelMapper = modelMapper;
    }

    // 메뉴 하나 조회
    public MenuDTO findMenuByCode(int menuCode) {
        /** findById 메소드는 id 값을 전달하여 해당 id의 엔터티를 조회
         * findById 의 반환값은 Optional 타입
         * 전달 된 id로 조회 된 Menu 엔터티가 없을 경우에는 IllegalArgumentException을 발생
         * ModelMapper의 map 메소드를 통해 Menu Entity를 MenuDTO로 변환해서 반환
         * */
        Menu menu = menuRepository.findById(menuCode).orElseThrow(IllegalArgumentException::new);
        return modelMapper.map(menu, MenuDTO.class);
    }

    //메뉴 전체 조회
    public List<MenuDTO> findMenulist(){
        /**전체 엔터티 조회 findAll 메소드
         * Sort.by 정렬, menuCode 필드 내림차순으로 처리
         * findAll 메소드의 반환 타입은 List<Menu>인데
         * 해당 타입을 List<MenuDTO>로 변경하기 위해서 stream화 시키고
         * map 메소드를 통해 하나의 Menu Entity마다 MenuDTO로 변환시키는 코드를 수행
         * 수행 완료한 stream을 다시 List로 변환 시켜 반환
         */
        List<Menu> menuList = menuRepository.findAll(Sort.by("menuCode").descending());
        return menuList.stream().map(menu -> modelMapper.map(menu, MenuDTO.class)).
                collect(Collectors.toList());
    }

    //페이징 처리
    public Page<MenuDTO> findMenuList(Pageable pageable) {
        /**  Pageable 객체를 이용하면 페이징 처리와 정렬을 동시에 처리
         * Pageable 객체를 생성할 때 PageRequest.of 메소드를 이용하여 생성
         * Sort 객체를 전달하여 정렬 순서를 지정
         * Pageable 객체를 생성하고 findAll 메소드의 파라미터를 Sort에서 Pageable로 변경
         * 반환 타입은 List<Menu> 가 아닌 Page<Menu>
         * Page 타입의 map 메소드를 사용해 Menu Entity를 MenuDTO 타입으로 변환
         * */

        /* page 파라미터가 Pageable의 number 값으로 넘어오는데 해당 값이 조회시에는
         인덱스 기준이 되어야 해서 -1 처리가 필요하다. */
        pageable = PageRequest.of(
                pageable.getPageNumber() <= 0 ? 0: pageable.getPageNumber() -1,
                pageable.getPageSize(),
                Sort.by("menuCode").descending());

        Page<Menu> menuList = menuRepository.findAll(pageable);
        return menuList.map(menu -> modelMapper.map(menu, MenuDTO.class));

    }
}
