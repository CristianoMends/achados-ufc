import { IsOptional, IsString } from "class-validator";
import { User } from "../entities/user.entity";

export class ViewUserDto {
    id: number;
    username: String;
    email: String;

    @IsString()
    name: string;

    @IsOptional()
    @IsString()
    phone: string | null;

    @IsOptional()
    @IsString()
    imageUrl: string | null;

    @IsOptional()
    @IsString()
    surname: string | null;

    constructor(user: User) {
        this.id = user.id;
        this.username = user.username;
        this.email = user.email;
        this.name = user.name;
        this.phone = user.phone;
        this.imageUrl = user.imageUrl;
        this.surname = user.surname;
    }
}
