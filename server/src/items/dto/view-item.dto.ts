import { ViewUserDto } from 'src/users/dto/view-user.dto';
import { Item } from '../entities/item.entity';

export class ViewItemDto {
    id: number;
    title: string;
    description: string;
    imageUrl: string;
    location: string;
    date: string;
    isFound: boolean;
    user: ViewUserDto;

    constructor(item: Item) {
        this.id = item.id;
        this.title = String(item.title);
        this.description = String(item.description);
        this.imageUrl = String(item.imageUrl);
        this.location = String(item.location);
        this.date = item.date.toString();
        this.isFound = !!item.isFound;
        this.user = new ViewUserDto(item.user);
    }
}
